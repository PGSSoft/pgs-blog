using System;
using System.Collections.Generic;
using System.Net.Http;
using FluentScheduler;
using HttpCalculatorClient;
using Microsoft.Extensions.DependencyInjection;
using ServicesContainer;

namespace ServiceDiscovery
{
    using Microsoft.AspNet.Hosting.Internal;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.Logging;
    using ServiceBase;
    using ServiceBootstrapper;
    using System.Threading;

    public class Startup : StartupBase
    {
        public override void ConfigureServices(IServiceCollection services)
        {
            services.AddMvc();
            services.Add(new ServiceDescriptor(typeof(ICalculatorHttpClient), typeof(HttpCalculatorClientImplementation), ServiceLifetime.Scoped));
            services.Add(new ServiceDescriptor(typeof(ICalculatorServiceContainer),
                _ => CalculatorServiceContainer.Instance, ServiceLifetime.Singleton));
        }        

        public static void Main(string[] args)
        {
            IConfiguration config = ServiceBootstrapper.BuildConfiguration(args);
            IApplication app = ServiceBootstrapper.StartApplication(config);
            ILogger logger = app.Services.GetService<ILogger<Startup>>();

            TaskManager.Initialize(new MyRegistry(app.Services.GetService<ILoggerFactory>()));

            logger.LogInformation("Started the server...");
            logger.LogInformation($"Now listening on: {config["server.urls"]}");
            logger.LogInformation("Application started.Press Ctrl + C to shut down.");

            using (WaitHandle handle = ServiceBootstrapper.DisposeOnInterupt(app))
            {
                handle.WaitOne();
            }
        }
    }


    public class MyRegistry : Registry
    {
        public MyRegistry(ILoggerFactory loggerFactory)
        {
            var task = new MyTask(loggerFactory.CreateLogger("ServiceRegistry"));

            Schedule(task.Execute)
                .NonReentrant()
                .ToRunNow()
                .AndEvery(5).Seconds();
        }
    }

    public class MyTask : ITask
    {
        private ILogger _logger;

        public MyTask(ILogger logger)
        {
            _logger = logger;
        }

        public async void Execute()
        {
            var healthyServices = new Dictionary<KeyValuePair<string, string>, List<string>>();
            var httpClient = new HttpClient();
            var allServices = CalculatorServiceContainer.Instance.GetAllServices();
            foreach (var key in allServices.Keys)
            {
                var allService = allServices[key];
                AssureKeyValueExists(healthyServices, key);
                foreach (var service in allService)
                {
                    var healthAddress = $"{service}/api/health";
                    try
                    {
                        var s = await httpClient.GetAsync(healthAddress);
                        if (s.IsSuccessStatusCode)
                        {
                            _logger.LogInformation($"[OK]{key.Key} {key.Value}");
                            healthyServices[key].Add(service);
                        }
                        else
                        {
                            _logger.LogWarning($"[NOK]{key.Key} {key.Value}");
                        }
                    }
                    catch (Exception e)
                    {
                        _logger.LogWarning($"[NOK]{key.Key} {key.Value}");
                    }
                }
            }
            CalculatorServiceContainer.Instance.UseAsNewAvailableServices(healthyServices);

        }

        private static void AssureKeyValueExists(Dictionary<KeyValuePair<string, string>, List<string>> servicesToRemove, KeyValuePair<string, string> key)
        {
            if (!servicesToRemove.ContainsKey(key) || servicesToRemove[key] == null)
            {
                servicesToRemove[key] = new List<string>();
            }
        }
    }
}


