namespace HttpCalculatorClient
{
    using System.Collections.Generic;
    using System.Net.Http;
    using System.Threading.Tasks;
    using CalculationRequestParser;

    public interface ICalculatorHttpClient
    {
        Task<HttpResponseMessage> GetHealthStatusOnApiUrl(string url);
        Task<string> GetStringAsync<T>(List<string> serviceAddress, CalculationOperation substraction, T v1, T v2);
        Task<string> GetServicesString(CalculationOperation operation, string version);
        Task<string> GetOperationServices(string version);
        Task<string> GetCalculationOperations(List<string> list, string input, string version);
    }
}
