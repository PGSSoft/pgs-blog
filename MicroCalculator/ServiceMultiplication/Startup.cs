using CalculationRequestParser;
using HttpCalculatorClient;
using ServiceFinder;

namespace ServiceMultiplication
{
    using Microsoft.Extensions.DependencyInjection;
    using ServiceBase;
    using ServiceBootstrapper;

    public class Startup : StartupBase
    {
        // This method gets called by the runtime. Use this method to add services to the container.
        // For more information on how to configure your application, visit http://go.microsoft.com/fwlink/?LinkID=398940
        public override void ConfigureServices(IServiceCollection services)
        {
            base.ConfigureServices(services);

            services.Add(new ServiceDescriptor(typeof(ICalculatorServiceFinder), typeof(CalculatorServiceFinder), ServiceLifetime.Transient));
            services.Add(new ServiceDescriptor(typeof(ICalculatorHttpClient), typeof(HttpCalculatorClientImplementation), ServiceLifetime.Scoped));
        }
        
        // Entry point for the application.
        public static void Main(string[] args) => ServiceBootstrapper.Main(CalculationOperation.Multiplication, "1.0", args);
    }
}
