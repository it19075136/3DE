import fb from '../Database/dbConfig';

const initState = {
    images: ['test', 'test1'],
    loading: false,
    imgCount: 0
}

export default function (state = initState, action) {
    switch (action.type) {
        case "GET_IMAGES":
            return {
                ...state,
                images: action.payload,
                loading: false
            }
        case "DELETE_IMAGE":
            return {
                ...state,
                images: state.menus.filter(image => image._id !== action.payload)
            }
        case "ADD_IMAGES":
            return {
                ...state,
                images: [action.payload, ...state.images]
            }
        case "IMAGES_LOADING":
            return {
                ...state,
                loading: true
            }
        case "COMPLETE":
            return {
                ...state,
                loading: false,
                imgCount: action.payload
            }
        default:
            return state;
    }
}
