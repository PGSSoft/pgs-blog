namespace CalculationRequestParser
{
    using System;

    public enum CalculationOperation
    {
        Unknown = 0,
        Addition = 1,
        Substraction = 2,
        Multiplication = 3,
        Division = 4
    }

    public static class CalculationOperationExtensions
    {
        public static string AsMathSymbol(this CalculationOperation operation)
        {
            switch (operation)
            {
                case CalculationOperation.Addition:
                    return "+";
                case CalculationOperation.Substraction:
                    return "-";
                case CalculationOperation.Multiplication:
                    return "*";
                case CalculationOperation.Division:
                    return "/";
                default:
                    throw new ArgumentOutOfRangeException(nameof(operation), operation, null);
            }
        }
    }
}
