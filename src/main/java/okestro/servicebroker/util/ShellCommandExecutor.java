/*
 * Developed by bhhan@okestro.com on 2020-05-25
 * Last modified 2020-05-25 16:59:31
 */

package okestro.servicebroker.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ShellCommandExecutor {
	/**
	 * exec new sub-process
	 */
	public void exec(String path) throws Exception {
		ProcessBuilder builder = new ProcessBuilder();
		if(new File(path).exists()) {
			// check os
			if (isWindow()) {
				builder.command("cmd.exe", "/c", path);
			} else {
				builder.command("sh", "-c", path);
			}

			// set error stream
			builder.redirectErrorStream(true);

			// start process
			Process process = builder.start();

			// get input stream
			InputStream is = process.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8.name()));
			String line = br.readLine();

			while (line != null) {
				// log
				log.info(line);

				// read line
				line = br.readLine();
			}

			// waiting
			process.waitFor();

			// check exit code
			int exitCode = process.exitValue();

			if (exitCode == 0) {
				//@ success
			} else if (exitCode == 126) {
				// permission denied
				throw new Exception("permission denied");
			} else if (exitCode == 127) {
				// file not found
				throw new Exception(path + " not found");
			} else {
				// unknown
				throw new Exception("unknown exception[" + exitCode + "]" );
			}

		} else {
			log.info("path : {} is not a valid", path);
		}
	}

	/**
	 * is window?
	 */
	private boolean isWindow() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
}
