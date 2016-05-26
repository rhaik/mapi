<#import "/lib/util.ftl" as util>
"error":{
             "request":"${requestUrl}",            
             "code":${error_code},                   
             "message":"${util.jsonQuote(error_message!"")}"        
       }   