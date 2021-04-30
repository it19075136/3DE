  import firebase from 'firebase'
  
  // Your web app's Firebase configuration
  let firebaseConfig = {
    apiKey: "AIzaSyBdXTCnelDAEe7lSl8UwguuDD0efU8ezxY",
    authDomain: "project-3de-eb7dd.firebaseapp.com",
    databaseURL: "https://project-3de-eb7dd-default-rtdb.firebaseio.com",
    projectId: "project-3de-eb7dd",
    storageBucket: "project-3de-eb7dd.appspot.com",
    messagingSenderId: "967906533329",
    appId: "1:967906533329:web:00defde43617cb3b148af0"
  };
  // Initialize Firebase
  const fb = firebase.initializeApp(firebaseConfig);

  export default fb;