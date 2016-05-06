using System.Collections.Generic;
using System.Threading.Tasks;

namespace CalculationRequestParser
{
    public interface IOperationParser
    {
        Task<List<CalculatorOperation>> ParseForCalculationOperations(string operation);
    }
}