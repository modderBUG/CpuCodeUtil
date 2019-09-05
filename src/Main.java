public class Main {

    public static void main(String[] args) {
        System.out.println(CpuCodeUtil.getCpuId());  //这个方法也可以适用于Linux
        System.out.println(CpuCodeUtil.getCpuId("sudo dmidecode -t 4"));  //防止出现意外bug手动输入命令吧！
    }
}
