namespace ServiceAddition
{
    using ServiceBootstrapper;
    using ServiceBase;
    using CalculationRequestParser;

    public class Startup : StartupBase
    {
        // Entry point for the application.
        public static void Main(string[] args) => ServiceBootstrapper.Main(CalculationOperation.Addition, "1.0", args);
    }
}
