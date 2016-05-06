using log4net;
using log4net.Config;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.PlatformAbstractions;
using System.IO;

namespace Log4netLogger
{
    public static class LoggerExtensions
    {
        public static IApplicationEnvironment ConfigureLog4net(this IApplicationEnvironment env, string configFileName = "log4net.xml")
        {
            GlobalContext.Properties["appRoot"] = env.ApplicationBasePath;
            XmlConfigurator.ConfigureAndWatch(new FileInfo(Path.Combine(env.ApplicationBasePath, configFileName)));

            return env;
        }

        public static ILoggerFactory AddLog4net(this ILoggerFactory loggerFactory)
        {
            loggerFactory.AddProvider(new Log4NetLoggerProvider());
            return loggerFactory;
        }
    }
}
