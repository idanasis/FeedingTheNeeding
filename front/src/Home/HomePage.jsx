import React from 'react';
import Header from '../Home/components/Header';
import Introduce from '../Home/components/Introduce';
import About from '../Home/components/About';
import Projects from '../Home/components/Projects';
import Pictures from '../Home/components/Pictures';
import ThingsDo from '../Home/components/ThingsDo';
import SayOnUs from '../Home/components/SayOnUs';
import '../Home/styles/home.css';

const HomePage = () => {
    return (
        <>
        <Header/>
        <Introduce/>
        <About/>
        <Projects/>
        <Pictures/>
        <ThingsDo/>
        <SayOnUs/>
        </>
    );
};

export default HomePage;