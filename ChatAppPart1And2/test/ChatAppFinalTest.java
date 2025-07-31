/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import java.util.ArrayList;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rahul
 */
public class ChatAppFinalTest {
    
    public ChatAppFinalTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ChatAppFinal.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ChatAppFinal.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reportMenu method, of class ChatAppFinal.
     */
    @Test
    public void testReportMenu() {
        System.out.println("reportMenu");
        Scanner s = null;
        ArrayList<Message> messages = null;
        ArrayList<String> hashes = null;
        ArrayList<String> ids = null;
        ChatAppFinal.reportMenu(s, messages, hashes, ids);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
