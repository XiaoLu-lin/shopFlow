package org.ysling.litemall.wx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ysling.litemall.core.utils.RandomStrUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;

@RunWith(SpringJUnit4ClassRunner.class)
public class BigDecimalTest {

    @Test
    public void test() {

        int num1 = 43;
        int num2 = 51;

        BigDecimal devide = BigDecimal.valueOf(num1).divide(BigDecimal.valueOf(num2) , 2 , BigDecimal.ROUND_CEILING);
        System.out.println(devide);

        //将结果百分比
        NumberFormat percent =NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);

        String format = percent.format(devide.doubleValue());

        System.out.println(percent.format(devide.doubleValue()));

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        String result1 = numberFormat.format((float) num1 / (float) (num1 + num2) * 100);
        String result2 = numberFormat.format((float) num2 / (float) (num1 + num2) * 100);

        System.out.println("num1和num2的百分比为:" + result1 + "%");
        System.out.println("num1和num2的百分比为:" + result2 + "%");

        BigDecimal a = BigDecimal.valueOf(0);
        BigDecimal b = BigDecimal.valueOf(1);
        BigDecimal c = a.subtract(b);
        BigDecimal d = c.max(BigDecimal.valueOf(0));

        System.out.println(c);
        System.out.println(d);

        String randomString = RandomStrUtil.getRandom(32, RandomStrUtil.TYPE.CAPITAL_NUMBER);
        System.out.println(randomString);
    }
}
