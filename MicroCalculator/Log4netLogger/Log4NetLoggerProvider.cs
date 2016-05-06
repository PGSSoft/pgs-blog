using System;
using Microsoft.Extensions.Logging;
using System.Collections.Concurrent;

namespace Log4netLogger
{
    public class Log4NetLoggerProvider : ILoggerProvider
    {
        private ConcurrentDictionary<string, ILogger> _loggers = new ConcurrentDictionary<string, ILogger>();

        public ILogger CreateLogger(string categoryName)
        {
            return _loggers.GetOrAdd(categoryName, name => new Log4netLoggerAdapter(name));
        }

        public void Dispose()
        {
            _loggers.Clear();
            _loggers = null;
        }
    }
}