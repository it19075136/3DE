import './App.css';
import { createStore, applyMiddleware, compose } from 'redux';
import { Provider } from 'react-redux';
import thunk from 'redux-thunk';
import { BrowserRouter, Route } from 'react-router-dom'
import rootReducer from './reducers/index';
import addImagesForm from './pages/addImagesForm';

const initialState = {};

const middleware = [thunk];

const store = createStore(rootReducer,initialState,compose(
    applyMiddleware(...middleware)
)
);

function App() {
  return (
    <Provider store={store}>
      <BrowserRouter>
          <Route path="/" exact component={addImagesForm} />
        </BrowserRouter>
    </Provider>
  );
}

export default App;
