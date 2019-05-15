package net.liuzd.java.mail;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.liuzd.java.mail.actuator.TestMailActuator;

@RunWith(Suite.class)  
@SuiteClasses({ 
    TestMailActuator.class
})  
public class StartTest {

}
  