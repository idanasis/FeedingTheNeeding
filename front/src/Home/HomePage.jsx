import React from 'react';
import Header from '../Home/components/Header';
import Introduce from '../Home/components/Introduce';
import About from '../Home/components/About';
import Projects from '../Home/components/Projects';
import Pictures from '../Home/components/Pictures';
import ThingsDo from '../Home/components/ThingsDo';
import SayOnUs from '../Home/components/SayOnUs';
import DonateUs from './components/DonateUs';
import Contact from './components/Contact';
import Footer from './components/Footer';

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
        <DonateUs/>
        <Contact/>
        <Footer/>

        </>
    );
};

export default HomePage;