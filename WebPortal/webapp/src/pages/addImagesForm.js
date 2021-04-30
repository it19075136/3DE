import React, { Component } from 'react';
import { connect } from 'react-redux'
import { addImages } from '../actions/imageActions'
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import { CircularProgress } from '@material-ui/core';

class addImagesForm extends Component {

    state = {
        files: [],
        selector: 'TCC'
    }

    changeHandler = (e) => {
        const inpFiles = document.getElementById('files');
        if (e.target.name === "files") {
            this.setState({
                ...this.state,
                files: inpFiles.files
            })
        }
        else {
            this.setState({
                [e.target.name]: e.target.value
            })
        }
    }

    onSubmit = (e) => {
        e.preventDefault();
        console.log(this.state);
        this.props.addImages(this.state);
    }

    render() {
        return (
            <Container>
                <Typography>
                    <h3>Add all Images for test scenarios</h3>
                </Typography>
                <br /><br />
                <form onSubmit={this.onSubmit} >
                    <label for="files">Choose files:</label><br />
                    <input type="file" id="files" name="files" onChange={this.changeHandler} multiple></input><br /><br />
                    <label for="selector">Select a type:</label><br />
                    {/* <div id="count">TCC image count :- {this.props.images.imgCount}</div> */}
                    <select name="selector" id="selector" onChange={this.changeHandler} >
                        <option id="TCC">TCC</option>
                        <option id="OTHER">Other</option>
                    </select>
                    <br />
                    <br />
                    <button type="submit" value="submit" disabled={this.props.images.loading}>Submit Images</button>
                    <br /><br />
                    <div hidden={!this.props.images.loading}>
                        <h4>Upload is in progress</h4>
                        <CircularProgress variant="indeterminate" />
                    </div>
                </form>
            </Container>
        );
    }
}

const mapStateToProps = state => ({
    images: state.images
});

export default connect(mapStateToProps, { addImages })(addImagesForm);