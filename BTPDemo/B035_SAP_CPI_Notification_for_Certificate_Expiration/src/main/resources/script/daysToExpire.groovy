import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
// ==========================###add import class for date calculation =====================
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
// ========================================================================================
def Message processData(Message message) {
    //Body
    def map = message.getProperties();
    String tenantName = System.getenv("TENANT_NAME");
   
    String certificateDetails = ""
    String certificateDetails_list = ""
    String finalhtmlBody = ""
    String environmentName = ""
    def body = message.getBody(java.lang.String);
    // ========================================call the property to set the mail body===============================
    def p_EmailBody = map.get("P_EmailBody");
    def p_EmailBody_Table  = map.get("P_EmailBody_Table");
    def p_EmailBody_Signature = map.get("P_EmailBody_Signature")  
    def P_ThresholdDays  = map.get("P_ThresholdDays");
    
    // ====================================logic for the environment name=======================================
   switch(tenantName) {
  case { it.contains('dev') }:
    environmentName = "DEV"
    break
  case { it.contains('formal') }:
    environmentName = "FORMAL"
    break
  case { it.contains('production') }:
    environmentName = "PROD"
    break    
  default:
    environmentName = "INFORMAL"
    break
}
 message.setProperty("environmentName",environmentName);
    // =====================================================================================================
   
    int value = P_ThresholdDays.toInteger()
    def root = new XmlSlurper().parseText(body);
    root.KeystoreEntry.each{ Msg ->
    long certExpirydateDate = new SimpleDateFormat("yyyy-MM-dd").parse(Msg.ValidNotAfter.toString()).getTime();
    long dateNow = System.currentTimeMillis();
    long dateDiff = certExpirydateDate - dateNow;
    def daysToExpire = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
    // ==================================check the certificate details based on ThresholdDays and create the table ==============================
    if (daysToExpire< value){
 certificateDetails_list =  '''
   <tr>
    <td style="width:30%">''' + Msg.Alias.toString() + '''</td>
    <td style="width:30%">''' + Msg.ValidNotAfter.toString() + '''</td>
    <td style="width:20%">''' + daysToExpire + '''</td>
  </tr>

''' 
certificateDetails = certificateDetails + certificateDetails_list 
    }
    }
    // ========================================================
//   ===============================Creating the mail body================================================== 
   finalhtmlBody = p_EmailBody + P_ThresholdDays + p_EmailBody_Table + certificateDetails + p_EmailBody_Signature
     message.setBody(finalhtmlBody);
    return message;
}