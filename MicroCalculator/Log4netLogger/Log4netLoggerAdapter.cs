using System;
using Microsoft.Extensions.Logging;
using log4net;

namespace Log4netLogger
{
    internal class Log4netLoggerAdapter : ILogger
    {
        private ILog _innerLogger;

        public Log4netLoggerAdapter(string name)
        {
            _innerLogger = LogManager.GetLogger(name);         
        }

        public IDisposable BeginScopeImpl(object state)
        {
            return null;            
        }

        public bool IsEnabled(LogLevel logLevel)
        {
            switch (logLevel)
            {
                case LogLevel.None:
                    return false;
                case LogLevel.Debug:
                case LogLevel.Verbose:
                    return _innerLogger.IsDebugEnabled;
                case LogLevel.Information:
                    return _innerLogger.IsInfoEnabled;
                case LogLevel.Warning:
                    return _innerLogger.IsWarnEnabled;
                case LogLevel.Error:
                    return _innerLogger.IsErrorEnabled;
                case LogLevel.Critical:
                    return _innerLogger.IsFatalEnabled;
                default:
                    throw new ArgumentException($"Unkown log level {logLevel}", nameof(logLevel));
            }
        }

        public void Log(LogLevel logLevel, int eventId, object state, Exception exception, Func<object, Exception, string> formatter)
        {
            if (!IsEnabled(logLevel))
            {
                return;
            }

            string message = (formatter ?? LogFormatter.Formatter)(state, exception);

            switch (logLevel)
            {
                case LogLevel.None:
                    return;
                case LogLevel.Debug:
                case LogLevel.Verbose:
                    _innerLogger.Debug(message, exception);
                    break;
                case LogLevel.Information:
                    _innerLogger.Info(message, exception);
                    break;
                case LogLevel.Warning:
                    _innerLogger.Warn(message, exception);
                    break;
                case LogLevel.Error:
                    _innerLogger.Error(message, exception);
                    break;
                case LogLevel.Critical:
                    _innerLogger.Fatal(message, exception);
                    break;
                default:
                    _innerLogger.Warn($"Unknown log level {logLevel}. Logging as info");
                    _innerLogger.Info(message, exception);
                    break;
            }
        }
    }
}