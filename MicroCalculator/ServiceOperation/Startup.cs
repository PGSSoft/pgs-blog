using CalculationRequestParser;
using HttpCalculatorClient;
using Microsoft.Extensions.DependencyInjection;

namespace ServiceOperation
{
    using Microsoft.AspNet.Hosting.Internal;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.Logging;
    using Registrator;
    using ServiceBase;
    using ServiceBootstrapper;
    using System.Threading;
    using System.Threading.Tasks;

    public class Startup : StartupBase
    {
        // This method gets called by the runtime. Use this method to add services to the container.
        // For more information on how to configure your application, visit http://go.microsoft.com/fwlink/?LinkID=398940
        public override void ConfigureServices(IServiceCollection services)
        {
            base.ConfigureServices(services);

            services.Add(new ServiceDescriptor(typeof(IOperationParser), typeof(OperationParser), ServiceLifetime.Scoped));
            services.Add(new ServiceDescriptor(typeof(ICalculatorHttpClient), typeof(HttpCalculatorClientImplementation), ServiceLifetime.Scoped));
        }

        // Entry point for the application.
        public static void Main(string[] args)
        {
            IConfiguration config = ServiceBootstrapper.BuildConfiguration(args, ServiceBootstrapper.BuildConfigurationOptions.RandomizeUrls);
            IApplication app = ServiceBootstrapper.StartApplication(config);
            ILogger logger = app.Services.GetService<ILogger<Startup>>();

            logger.LogInformation("Started the server...");
            logger.LogInformation($"Now listening on: {config["server.urls"]}");
            logger.LogInformation("Application started.Press Ctrl + C to shut down.");


            var registrator = new ServiceRegistrator(config);
            registrator.RegisterOperationService("1.0").ContinueWith(task =>
            {
                if (task.Status == TaskStatus.RanToCompletion)
                {
                    logger.LogInformation($"Service operation 1.0 registered with url {registrator.Url}");
                }
                else
                {
                    logger.LogCritical($"Failed to register service operation 1.0 with url {registrator.Url}", task.Exception);
                }
            });

            using (WaitHandle handle = ServiceBootstrapper.DisposeOnInterupt(app))
            {
                handle.WaitOne();
            }
        }
    }
}
