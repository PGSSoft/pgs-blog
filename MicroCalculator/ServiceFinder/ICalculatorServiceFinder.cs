using System.Collections.Generic;
using System.Threading.Tasks;
using CalculationRequestParser;

namespace ServiceFinder
{
    public interface ICalculatorServiceFinder
    {
        Task<List<string>> FindByCalculatorOperation(CalculationOperation servicename, string version);
        Task<List<string>> FindCalculationService(string version);
    }
}
