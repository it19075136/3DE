const nodemailer = require("nodemailer");
const { MAIL, PWD } = require('../config/mail')

function send(payload) {

    let recipients = "";
    let type = "Attention"

    // add guardian mail addresses to the recipients string
    for (let index = 0; index < JSON.parse(payload.recipients).length; index++) {
        recipients = recipients + JSON.parse(payload.recipients)[index] + ",";
    }

    if(payload.type === 'final')
      type = 'Dementia'
    // create message body
    const messageBody = "User details\n\nName:- "+payload.patient.name+"\nDate of Birth:- "+payload.patient.age+`\n\nUser's ${type} status,\n`+payload.results+"\n"+payload.suggestions+"\n\nYours' sincerely,\n3DE Team ❤"
    
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
        subject: `${type} Results of `+payload.patient.name, // Subject line
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