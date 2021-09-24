const nodemailer = require("nodemailer");
const { MAIL, PWD } = require('../config/mail')

function send(payload) {

    let recipients = "";

    // add guardian mail addresses to the recipients string
    for (let index = 0; index < payload.recipients.length; index++) {
        recipients = recipients + payload.recipients[index] + ",";
    }

    // create message body
    const messageBody = "User details\n\n"+payload.patient.name+"\n"+payload.patient.age+" Years\n\nUser dementia status:"+payload.results+"\n"+payload.suggestions+"\n\nYours' sincerely,\n3DE Team ❤"
    
    // create reusable transporter object using the default SMTP transport
    let transporter = nodemailer.createTransport({
        service: "gmail",
        auth: {
            user: MAIL,
            pass: PWD, 
        }
    });

    // Creating mail content
    let mailOptions ={
        from: MAIL, // sender address
        to: recipients, // list of receivers
        subject: "Dementia Results of "+payload.patient.name, // Subject line
        text: messageBody, // plain text body
      }

    // send mail with defined transport object
    transporter.sendMail(mailOptions,function(error,info){
        if(error){
          console.log(error);
          return (error);
        }else{
          console.log('Email sent: ' + info.response);
        }
      });

}

module.exports = { send }