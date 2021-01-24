import React, {useCallback, useEffect, useState} from "react";
import './App.css';
import axios from "axios";
import {useDropzone} from 'react-dropzone'

const UserProfiles = () => {

    //sets the state of userProfiles with the useState method, inital state is an empty array
    const [userProfiles, setUserProfiles] = useState([]);

    //setting the state of the userProfiles with the fetchUserProfiles method
    //Utilizing the axios library for restful calls from the frontend "get" is retrieving the userProfile from the api
    const fetchUserProfiles = () => {
        axios.get("http://localhost:8080/api/v1/user-profile").then(res => {
            //"http://localhost:8080/api/v1/user-profile" is the endpoint located in the controller class
            console.log(res);
            //setting the userProfiles with the resulting data retrieved from the server
            setUserProfiles(res.data);
        });
    }

    //useEffect is call the fetchUserProfiles upon loading of the webpage
    //this will occur only once as there is not dependency set in the useEffect
    useEffect(() => {
        fetchUserProfiles();
    }, []);


    //returning all userProfiles with an index so we can access the data, without the index we would not be able to access the data
    return userProfiles.map((userProfile, index) => {
        return (
            //the key is what allows the indexing of the data
            //accessing each userProfile id by referencing the object then the objects id
            <div key={index}>
                {userProfile.userProfileId ?
                    //image source is downloaded from the backend, if there is no image it is nul
                    // : null is the else in the below line of code
                    <img src={`http://localhost:8080/api/v1/user-profile/${userProfile.userProfileId}/image/download`} /> : null}
                <br/>
                <br/>
                {/*getting the username from userProfile by first referencing it then the field that is to be displayed,
                same for the userProfileId*/}
                <h1>{userProfile.username}</h1>
                <p>{userProfile.userProfileId}</p>
                {/*call the dropzone function. Using property spread notation below, reduces the need for additional characters when obtaining information. Example below
                 this: <Modal {...person} title='Modal heading' animation={false} />
                 equals to this: <Modal name={person.name} age={person.age} title='Modal heading' animation={false} />*/}
                <Dropzone {...userProfile}/>
                <br/>
            </div>
        );
    });
};

//imported a library from the react-dropzone github sourcecode. Allows a user to drop files into a location and upload them to a database
function Dropzone({userProfileId}) {
    //call the useCallback to a variable
    const onDrop = useCallback(acceptedFiles => {
        //creating a file variable from the file uploaded
        const file = acceptedFiles[0];

        console.log(file);

        //using formData to create the file name
        const formData = new FormData();
        formData.append("file", file);

        //using the axios library to upload an image with its name to the S3 buckets database
        axios.post(`http://localhost:8080/api/v1/user-profile/${userProfileId}/image/upload`,
            formData,
            {
                //ensuring proper headers for upload
                headers: {
                    "Consent-Type": "multipart/form-data"
                }
            }
        ).then(() => {
            //print upon successful upload
            console.log("file uploaded successfully")
        }).catch(err => {
            console.log(err);
        });
    }, []);

    //from react-dropzone
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        //using spread operator to get rootProps and inputProps
        <div {...getRootProps()}>
            <input {...getInputProps()} />
            {isDragActive ? (
                //message that appears when hovering over the dropzone with an image
                <p>Drop the image here ...</p>
            ) : (
                //message that is always present as a prompt for the user
                <p>Drag 'n' drop profile image, or click to select profile image</p>
            )}
        </div>
    )
}

function App() {
    return <div className="App">
        <UserProfiles/>
    </div>;
}

export default App;
