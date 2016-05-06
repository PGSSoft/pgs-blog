namespace CalculationRequestParser
{
    using System.Collections.Generic;
    using System.Text.RegularExpressions;
    using System.Threading.Tasks;

    public class OperationParser : IOperationParser
    {
        public async Task<List<CalculatorOperation>> ParseForCalculationOperations(string operation)
        {
            var r = new Regex(@"(?<operator>[\+\-\/\*])?(?<value>\-?\d+(\.\d*)?)");
            var matches = r.Matches(operation);
            var operations = this.ParseResults(matches);
            return operations;
        }

        private List<CalculatorOperation> ParseResults(MatchCollection match)
        {
            List<CalculatorOperation> operations = new List<CalculatorOperation>();

            for (int i = 0; i < match.Count; i++)
            {
                var r = match[i].ToString();
                var operation = char.IsDigit(r[0]) ? CalculationOperation.Addition : ExtractOperation(r[0]);
                var value = ExtractValue(char.IsDigit(r[0]) ? r.Substring(0) : r.Substring(1));
                operations.Add(new CalculatorOperation(value, operation));
            }

            return operations;
        }

        private decimal ExtractValue(string value)
        {
            return decimal.Parse(value);
        }

        private CalculationOperation ExtractOperation(char c)
        {
            switch (c)
            {
                case '-': return CalculationOperation.Substraction;
                case '*': return CalculationOperation.Multiplication;
                case '/': return CalculationOperation.Division;
                case '+':
                default: return CalculationOperation.Addition;
            }
        }
    }
}