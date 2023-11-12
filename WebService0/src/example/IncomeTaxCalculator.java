package example;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService()
public class IncomeTaxCalculator {

    @WebMethod
    public double calculateIncomeTax(double annualIncome) {
        double tax = 0;

        if (annualIncome <= 5000) {
            tax = 0;
        } else if (annualIncome <= 8000) {
            tax = (annualIncome - 5000) * 0.03;
        } else if (annualIncome <= 17000) {
            tax = (annualIncome - 8000) * 0.1 + 300; // 300是前两档税额之和
        } else {
            tax = (annualIncome - 17000) * 0.2 + 1300; // 1300是前三档税额之和
        }

        return tax;
    }

    public static void main(String[] argv) {
        Object implementor = new IncomeTaxCalculator();
        String address = "http://localhost:9001/IncomeTaxCalculator";
        Endpoint.publish(address, implementor);
        System.out.println("Web服务已发布，访问地址: " + address);
    }
}
