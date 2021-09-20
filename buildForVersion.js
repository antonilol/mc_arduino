const https = require('https');
const fs = require('fs');
const execSync = require('child_process').execSync;

const buildFolder = 'build/libs/';

function get(host, path, v) {
	return new Promise(r => {
		const req = https.request({
			hostname:  host,
			port:	   443,
			path:	   path,
			method:   'GET'
		}, res => {
			var data = '';
			res.on('data', chunk => { data += chunk; });
			res.on('end', () => { r({data, res, v}); });
		})

		req.on('error', error => {
			console.error(error);
		});

		req.end();
	});
}

function arrayToHumanString(a) {
	var output = '';
	const l = a.length;
	a.forEach((x, i) => {
		if (i == l - 1) {
			output += ' and ';
		} else if (i) {
			output += ', ';
		}
		output += x.toString();
	});
	return output;
}

function badXmlParse(xml) {
	var release;
	const versions = xml.split('\n').map(l => {
		const v = l.split('<version>')[1];
		const r = l.split('<release>')[1];
		if (!v) {
			if (r) {
				release = r.split('</release>')[0];
			}
			return false;
		}
		return v.split('</version>')[0];
	}).filter(x => x);

	return {
		versions,
		release
	};
}

function getBranch(version) {
	// from https://fabricmc.net/versions.html
	var branch = "1.15"
	//TODO make this better
	if(version === "1.14.4") {
	    branch = "1.14"
	}
	if (version.startsWith("1.16")) {
	    branch = "1.16"
	}
	if (version.startsWith("21w") || version.startsWith("20w") || version.startsWith("1.17")) {
	    branch = "1.17"
	}
	if (version === "1.16.1") {
	    branch = "1.16.1"
	}
	if (version === "20w14infinite") {
	    branch = "20w14infinite"
	}
	if (version.startsWith("1.18_experimental")) {
	    branch = "1.18_experimental"
	}

	return branch;
}

function getFabricVersion(v) {
	var latest = v.maven.versions.release;
	v.maven.versions.versions.forEach(version => {
		if (version.endsWith("-" + v.branch) || version.endsWith("+" + v.branch)) {
			latest = version;
		}
	});

	return latest;
}

function rm(path) {
	try {
		fs.unlinkSync(path);
	} catch (e) {
	}

	try {
		fs.rmdirSync(path, { recursive: true });
	} catch (e) {
	}
}

function logAddLine(log, lines) {
	const date = `[${(new Date()).toUTCString()}] `;
	const noDate = ' '.repeat(date.length);
	lines.split('\n').forEach((line, i) => {
		if (i) {
			log += noDate + line + '\n';
		} else {
			log += date + line + '\n';
		}
	});
	return log;
}

const progressMsg = (p, t) => `\rRequesting mappings and loader versions... ${p}/${t}`;

async function main() {
	// TODO build 21w18a and earlier version with java 8
	rm('mcVersions');
	var maven14, maven15;

	await Promise.all([
		get('maven.fabricmc.net', '/net/fabricmc/fabric/maven-metadata.xml').then(x => {
			maven14 = {
				versions: badXmlParse(x.data),
				string: 'net.fabricmc:fabric:'
			};
		}),

		get('maven.fabricmc.net', '/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml').then(x => {
			maven15 = {
				versions: badXmlParse(x.data),
				string: 'net.fabricmc.fabric-api:fabric-api:'
			};
		})
	]);

	const args = process.argv.filter((x, i) => i > 1);

	const options = { };
	['stable', 'unstable', 'all', 'nolog'].forEach(x => {
		if (args.includes(x)) {
			options[x] = 1;
		}
	});

	console.log('Requesting game versions...');
	var java = 16;
	var maven = maven15;
	const versions = JSON.parse((await get('meta.fabricmc.net', '/v1/versions/game')).data)
		.map(x => {
			if (x.version == "21w18a") {
				java = 8;
			}
			x.java = java;

			if (x.version == "1.14") {
				maven = maven14;
			}
			x.maven = maven;

			x.branch = getBranch(x.version);
			return x;
		})
		.filter(x =>
			options.all ||
			options.stable && x.stable ||
			options.unstable && !x.stable ||
			args.includes(x.version)
		);

	console.log(`Versions: (${versions.length}) ${arrayToHumanString(versions.map(x=>x.version))}`);

	var progress = 0;
	process.stdout.write(progressMsg(0, versions.length));
	const loaderVersions = (await Promise.all(versions
		.map(v => get('meta.fabricmc.net', `/v1/versions/loader/${encodeURIComponent(v.version)}`, v)
			.then(x => {process.stdout.write(progressMsg(++progress, versions.length)); return x;}))))
		.map(x => Object.assign(JSON.parse(x.data)[0], x.v));

	console.log('\n');

	const prop = fs.readFileSync('gradle.properties').toString('utf-8');
	fs.copyFileSync('gradle.properties', 'gradle.properties.old');

	try {
		fs.readdirSync(buildFolder).forEach(file => {
			rm(buildFolder + file);
		});
	} catch (e) {
	}

	loaderVersions.forEach((v, i) => {
		v.fabricV = getFabricVersion(v);
		rm('.gradle');
		const newProp = prop
			.replace(/minecraft_version.*/, `minecraft_version = ${v.version}`)
			.replace(/yarn_mappings.*/,     `yarn_mappings = ${v.mappings.version}`)
			.replace(/loader_version.*/,    `loader_version = ${v.loader.version}`)
			.replace(/fabric_maven.*/,      `fabric_maven = ${v.maven.string}`)
			.replace(/fabric_version.*/,    `fabric_version = ${v.fabricV}`);

		fs.writeFileSync('gradle.properties', newProp);

		const folder = `mcVersions/`;
		var failed = false;
		if (!options.nolog) {
			var log = '';
			log = logAddLine(log,
				`Build started for Minecraft version ${v.version}\n` +
				`Gradle arguments:\n` +
				`minecraft_version = ${v.version}\n` +
				`yarn_mappings =     ${v.mappings.version}\n` +
				`loader_version =    ${v.loader.version}\n` +
				`fabric_maven =      ${v.maven.string}\n` +
				`fabric_version =    ${v.fabricV}`
			);
		}


		console.log(`\n\nBuilding for Minecraft version ${v.version}... (${i+1}/${loaderVersions.length})\n`);
		try {
			execSync('./gradlew build', { stdio: 'inherit' });
		} catch (e) {
			failed = true;
		}


		fs.mkdirSync(folder, { recursive: true });

		if (!options.nolog) {
			if (failed) {
				log = logAddLine(log, 'Build failed');
			} else {
				log = logAddLine(log, 'Build succesful');
			}
			fs.writeFileSync(folder + `build-mc${v.version}.log`, log);
		}


		if (!failed) {
			fs.readdirSync(buildFolder).forEach(file => {
				if (!file.includes('dev')) {
					fs.copyFileSync(
						buildFolder + file,
						folder + file.split('.jar')[0] + `-mc${v.version}.jar`
					);
				}
				rm(buildFolder + file);
			});
		}
	});

	fs.copyFileSync('gradle.properties.old', 'gradle.properties');
}

main();
