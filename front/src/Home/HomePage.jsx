import React from 'react';
import Header from '../Home/components/Header';
import Introduce from '../Home/components/Introduce';
import About from '../Home/components/About';
import '../Home//styles/home.css';

const HomePage = () => {
    return (
        <>
        <Header/>
        <Introduce/>
        <About/>
        </>
    );
};

export default HomePage;