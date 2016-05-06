namespace ServiceSubstraction
{
    using CalculationRequestParser;
    using ServiceBase;
    using ServiceBootstrapper;

    public class Startup : StartupBase
    {
                // Entry point for the application.
        public static void Main(string[] args) => ServiceBootstrapper.Main(CalculationOperation.Substraction, "1.0", args);        
    }
}
