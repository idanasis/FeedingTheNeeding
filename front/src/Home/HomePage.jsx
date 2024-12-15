import React from 'react';
import Header from '../Home/components/Header';
import Introduce from '../Home/components/Introduce';
import About from '../Home/components/About';
import Projects from '../Home/components/Projects';
import Pictures from '../Home/components/Pictures';
import '../Home/styles/home.css';

const HomePage = () => {
    return (
        <>
        <Header/>
        <Introduce/>
        <About/>
        <Projects/>
        <Pictures/>
        </>
    );
};

export default HomePage;