namespace CalculationRequestParser
{
    public class CalculatorOperation
    {
        public CalculatorOperation(decimal value, CalculationOperation operation)
        {
            Value = value;
            Operation = operation;
        }

        public decimal Value { get; private set; }
        public CalculationOperation Operation { get; private set; }
    }
}