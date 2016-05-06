using System.Collections.Generic;

namespace ServicesContainer
{
    public interface ICalculatorServiceContainer
    {
        void Register(string serviceName, string url, string version);
        List<string> Get(string serviceName, string version);
    }
}