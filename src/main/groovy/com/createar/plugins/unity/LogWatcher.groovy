package com.createar.plugins.unity

/**
 * Inteface for line watchers.
 */
public interface ILogWatcherDelegate {
	/**
	 * Called when a line has been modified.
	 *
	 * @param      line  The line that has been modified.
	 */
	void OnModified(String line);
}

/**
 * Forwards logs to Standard Out.
 */
public class StdoutLogWatcherDelegate implements ILogWatcherDelegate {
	/**
	 * Called when a line has been modified.
	 *
	 * @param      line  The line to forward.
	 */
	public void OnModified(String line) {
		System.out.println(line);
	}
}

/**
 * Throws an exception when a regex is matched.
 */
public class RegexMatchLogWatcherDelegate implements ILogWatcherDelegate {
	/**
	 * The regex to watch.
	 */
	private final String _regex;

	/**
	 * Creates a new regex delegate.
	 *
	 * @param      regex  The regular expression to watch for.
	 */
	public RegexMatchLogWatcherDelegate(String regex) {
		_regex = regex;
	}

	/**
	 * Called when a line has been modified.
	 *
	 * @param      line  The line that has been modified.
	 */
	public void OnModified(String line) {
		if (line.matches(_regex)) {
			throw new RuntimeException(line);
		}
	}
}

/**
 * Watches a log file and executes delegates on each new line.
 */
public class LogWatcher implements Runnable {

	/**
	 * The file to watch.
	 */
	private final File _file;

	/**
	 * The list of delegates added to this watcher.
	 */
	private final ArrayList<ILogWatcherDelegate> _delegates = new ArrayList<ILogWatcherDelegate>();

	/**
	 * Buffers reads from the file.
	 */
	private BufferedReader _reader;

	/**
	 * Watches when the file was last modified.
	 */
	private long _lastModified = -1;

	/**
	 * True iff the watcher is alive.
	 */
	public boolean isAlive;

	/**
	 * Creates a new watcher.
	 *
	 * @param      log   The log to watch.
	 */
	public LogWatcher(File log) {
		_file = log;
	}

	/**
	 * Adds a delegate to watch each line.
	 *
	 * @param      watcher  The watcher to add.
	 *
	 * @return     The LogWatcher.
	 */
	public LogWatcher addDelegate(ILogWatcherDelegate watcher) {
		_delegates.add(watcher);

		return this;
	}

	/**
	 * Runs the watcher in a loop.
	 */
	public void run() {
		isAlive = true;

		while (isAlive) {
			if (_file.exists()) {
				if (null == _reader) {
					_reader = new BufferedReader(new FileReader(_file));
				}
				
				while (true) {
					String line = _reader.readLine();
					if (null != line) {
						// call all delegates
						for (ILogWatcherDelegate watcher : _delegates) {
							watcher.OnModified(line);
						}
					} else {
						break;
					}
				}
			}

			Thread.sleep(100)
		}
	}
}