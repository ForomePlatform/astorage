package org.forome.astorage.service.main;

import org.forome.astorage.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;

public class MainService {

	private final static Logger log = LoggerFactory.getLogger(MainService.class);

	public static void main(String[] args) {
		ArgumentParser arguments;
		try {
			arguments = new ArgumentParser(args);
		} catch (Throwable e) {
			log.error("Exception arguments parser", e);
			System.exit(2);
			return;
		}

		try {
			Service service = new Service(
					arguments.port,
					(thread, throwable) -> crash(throwable)
			);

			FutureTask<Void> stopSignal = new FutureTask<>(() -> null);
			Runtime.getRuntime().addShutdownHook(new Thread(stopSignal, "shutDownHook"));
			stopSignal.get();

			service.stop();
		} catch (Throwable e) {
			crash(e);
		}
	}

	public static void crash(Throwable e) {
		log.error("Application crashing ", e);
		System.exit(1);
	}
}
