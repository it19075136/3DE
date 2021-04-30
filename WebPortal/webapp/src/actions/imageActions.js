import fb from '../Database/dbConfig'
import uuid from 'react-uuid'

export const addImages = images => dispatch => {

    let StorageRef = fb.storage();
    let DbRef = fb.database().ref('TCCtestCases/test1/images');
    let imgCount = 0;

    DbRef.on('value', (snapshot) => {
        const imgSet = snapshot.val();
        if(imgSet != null){
            console.log(imgSet.length);
            imgCount = imgSet.length;
        }
    });

    if (images.selector === "TCC")
        StorageRef = fb.storage().ref('TCC');
    else            
        StorageRef = fb.storage().ref('other');

    for (let index = 0; index < images.files.length; index++) {
        let id = uuid();
        let uploadTask = StorageRef.child(id).put(images.files[index]);

        uploadTask.on('state_changed', (snapshot) => {
            // in progress
            dispatch({
                type: "IMAGES_LOADING"
            })
        },
            (error) => {
                // on error
                console.log(error);

                // dispatch upload error
            },
            () => {
                // on complete
                StorageRef.child(id).getDownloadURL().then(url => {
                    if (images.selector === "TCC"){
                        DbRef.child(imgCount).set(url);                        
                    }
                    else
                        // other type images to be added from here
                        console.log("other");
                })
                dispatch({
                    type: "COMPLETE",
                    payload: imgCount
                })            
            });
    }

}

export const getImages = () => dispatch => {

}
export const deleteImage = id => dispatch => {

}



