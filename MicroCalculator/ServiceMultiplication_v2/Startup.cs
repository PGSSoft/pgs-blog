using CalculationRequestParser;
using HttpCalculatorClient;
using Microsoft.Extensions.DependencyInjection;
using ServiceFinder;

namespace ServiceMultiplication_v2
{
    using ServiceBase;
    using ServiceBootstrapper;

    public class Startup : StartupBase
    {
        public override void ConfigureServices(IServiceCollection services)
        {
            base.ConfigureServices(services);

            services.Add(new ServiceDescriptor(typeof(ICalculatorServiceFinder), typeof(CalculatorServiceFinder), ServiceLifetime.Transient));
            services.Add(new ServiceDescriptor(typeof(ICalculatorHttpClient), typeof(HttpCalculatorClientImplementation), ServiceLifetime.Scoped));
        }        

        public static void Main(string[] args)
        {
           ServiceBootstrapper.Main(CalculationOperation.Multiplication, "2.0", args);            
        }
    }
}
