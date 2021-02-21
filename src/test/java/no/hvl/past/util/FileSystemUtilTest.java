package no.hvl.past.util;

import no.hvl.past.TestBase;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class FileSystemUtilTest extends TestBase {

    @Test
    public void testOS() {
        requiresSpecificInstalledComponents();
        String osName = System.getProperty("os.name");
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            NetworkInterface inter;
            while (networks.hasMoreElements()) {
                inter = networks.nextElement();
                System.out.println(inter.getDisplayName());
                System.out.println(inter.getIndex());

                byte[] mac = inter.getHardwareAddress();
                if (mac != null) {
                    for (int i = 0; i < mac.length; i++) {
                        System.out.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
                    }
                    System.out.println("");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

}
