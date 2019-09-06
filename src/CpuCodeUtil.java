import java.io.*;

/***
 * 获取cpu ID的工具类
 * 针对不同系统需要调用不同的方法。
 * 根据自己需求来调用
 * update 20190905 吴晓伟
 * */


public class CpuCodeUtil {

    /**
     * 针对windows版本。获取cpuID
     * **/
    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            String path = file.getPath().replace("%20", " ");
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + path);
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }
    /**
     * Linux版本获取cpuid
     * @String cmd  命令，输入Linux命令返回Linux输出结果，必须安装dmidecode
     * 如何使用命令附录已经给出
     * **/

    public static String executeLinuxCmd(String cmd)  {
        try {
            //System.out.println("got cmd job : " + cmd);
            Runtime run = Runtime.getRuntime();
            Process process;
            process = run.exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }

            in.close();
            process.destroy();
            return out.toString();    //str.replaceAll("\\s*", "")
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 工具类测试类，描述具体实现方法。
     *
     * linux：
     * 1.输入命令获得字符串
     * 2.将字符串去除空格
     * 3.找到ID：所在位置
     * 4.提取ID:后面的16位字符
     *
     * **/

    /**
     * 通用的部分 直接获取
     * **/
    public static String getCpuId(){

        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            System.out.println("-System:"+os);
            System.out.println("|--Windows:"+CpuCodeUtil.getCPUSerial());
            return CpuCodeUtil.getCPUSerial();
        }else{
            System.out.println("-System:"+os);
            /**
             * 这里可能会提示输入密码导致程序错误。使用方法在下面。
             * */
            String str=CpuCodeUtil.executeLinuxCmd("sudo dmidecode -t 4");
            str=str.replaceAll("\\s*","");
            //System.out.println("|--debug0:"+str);
            //System.out.println("|--debug1:"+str.substring(str.indexOf("ID:"),str.indexOf("ID:")+19));
            str=str.substring(str.indexOf("ID:")+3,str.indexOf("ID:")+19);
            System.out.println("|--Linux:"+str);                              //.replaceAll("\\s*","")
            return str;
        }
    }

    /**
     * 针对linxu部分，输入命令来获取吧！
     * **/
    public static String getCpuId(String commend){
            String str=CpuCodeUtil.executeLinuxCmd(commend);
            str=str.replaceAll("\\s*","");
            str=str.substring(str.indexOf("ID:")+3,str.indexOf("ID:")+16);
            System.out.println("|--Linux:"+str);  //.replaceAll("\\s*","")
        return str;
    }


    public static void main(String[] args) {
        System.out.println(getCpuId());
    }
}


/** @String cmd获得信息命令
 *
 * // 获得CPU ID
 * dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'
 *
 * // 获得磁盘ID
 *         fdisk -l |grep "Disk identifier" |awk {'print $3'}
 *
 *         查看CPU信息
 *         cat /proc/cpuinfo
 *
 *         显示当前硬件信息
 *         sudo lshw
 *
 *         获取CPU序列号或者主板序列号
 *         #CPU ID
 *         sudo dmidecode -t 4 | grep ID
 *
 *         #Serial Number
 *         sudo dmidecode  | grep  Serial
 *
 *         #CPU
 *         sudo dmidecode -t 4
 *
 *         #BIOS
 *         sudo dmidecode -t 0
 *
 *         #主板：
 *         sudo dmidecode -t 2
 *
 *         #OEM:
 *         sudo dmidecode -t 11
 *
 *         显示当前内存大小
 *         free -m |grep "Mem" | awk '{print $2}'
 *
 *         查看硬盘温度
 *         sudo apt-get install hddtemp
 *         sudo hddtemp /dev/sda
 *
 *
 * **/
