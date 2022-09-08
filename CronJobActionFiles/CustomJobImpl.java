package CronJobActionFiles;
import com.github.datnguyenzzz.Interfaces.CustomJob;

public class CustomJobImpl implements CustomJob {
    private void pre(String yell) {
        System.out.println("Void invoked - " + yell);
    }
    public void execute() {
        pre("THIS IS SPARTA !!!!");
        System.out.println("THIS IS CUSTOM JOB MTFK !!!");
    }
}
