using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using NUnit.Framework;

namespace RegexTests
{
    [TestFixture]
    public class Class1
    {
        [Test]
        public void T001()
        {
            var r = new Regex(@"([\+\-\/\*])?(\-?\d+)");
            var m = r.Matches("1+1");
            Assert.AreEqual(2, m.Count);
        }

        [Test]
        public void T002()
        {
            var r = new Regex(@"([\+\-\/\*])?(\-?\d+)");
            var m = r.Matches("1+1+1+1+2+3+4");
            Assert.AreEqual(7, m.Count);
        }

        [Test]
        public void T003()
        {
            var r = new Regex(@"([\+\-\/\*])?(\-?\d+)");
            var m = r.Matches("1+1+1*-1+2/-3+4");
            Assert.AreEqual(7, m.Count);
        }

        [Test]
        public void T004()
        {
            var r = new Regex(@"(?<operator>[\+\-\/\*])?(?<value>\-?\d+)");
            var m = r.Matches("0+1+1+1*-1+2/-3+4");
            Assert.AreEqual(8, m.Count);

            foreach (var mm in m)
            {
                Console.WriteLine(mm);
            }
        }
    }
}
