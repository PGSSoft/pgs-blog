using CalculationRequestParser;
using Microsoft.AspNet.Hosting;
using Microsoft.AspNet.Hosting.Internal;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Registrator;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace ServiceBootstrapper
{
    public static class ServiceBootstrapper
    {    
        public enum BuildConfigurationOptions
        {
            None,
            RandomizeUrls
        }

        public static IConfiguration BuildConfiguration(string[] args, BuildConfigurationOptions options = BuildConfigurationOptions.None)
        {
            var builder = new ConfigurationBuilder();
            builder.AddCommandLine(args);            

            if (options == BuildConfigurationOptions.RandomizeUrls)
            {
                var baseConfiguration = builder.Build();

                builder = new ConfigurationBuilder();
                builder.Add(new UrlRadomizeConfigurationProvider(baseConfiguration));
            }

            return builder.Build();
        }

        public static IApplication StartApplication(IConfiguration configuration)
        {
            return new WebHostBuilder(configuration)
                .UseServer("Microsoft.AspNet.Server.WebListener")
                .Build()
                .Start();
        }

        public static WaitHandle DisposeOnInterupt(IApplication application)
        {
            var mre = new ManualResetEventSlim();

            Console.CancelKeyPress += (sender, eventArgs) =>
            {
                application.Dispose();
                eventArgs.Cancel = true;
                mre.Set();
            };

            return mre.WaitHandle;
        }

        public static void RegisterService(IConfiguration configuration, CalculationOperation operation, string version, ILogger logger)
        {
            var registrator = new ServiceRegistrator(configuration);
            registrator.Register(operation, version).ContinueWith(task =>
            {
                if (task.Status == TaskStatus.RanToCompletion)
                {
                    logger.LogInformation($"Service {operation} {version} registered with url {registrator.Url}");
                }
                else
                {
                    logger.LogCritical($"Failed to register service {operation} {version} with url {registrator.Url}", task.Exception);
                }
            });
        }

        public static void Main(CalculationOperation operation, string serviceVersion, string[] args)
        {
            IConfiguration config = BuildConfiguration(args, BuildConfigurationOptions.RandomizeUrls);
            IApplication app = StartApplication(config);
            ILogger logger = app.Services.GetService<ILoggerFactory>().CreateLogger("ServiceBootstrapper");

            logger.LogInformation($"Started the {operation} server...");
            logger.LogInformation($"Now listening on: {config["server.urls"]}");
            logger.LogInformation("Application started. Press Ctrl + C to shut down.");

            RegisterService(config, operation, serviceVersion, logger);

            using (WaitHandle handle = DisposeOnInterupt(app))
            {
                handle.WaitOne();
            }
        }
    }
}
