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
import SEOHelmet from '../SEO/Components/SEOHelmet';
import '../Home/styles/home.css';

const HomePage = () => {
    return (
        <>
         <SEOHelmet 
        title="להשביע את הלב" 
        description="עזרה לאנשים נזקקים על ידי תרומת מזון, נסיעות או תרומה כספית דרך הפלטפורמה שלנו שמחברת בין תורמים לאנשים שזקוקים לעזרה."
        keywords="להשביע את הלב, תרומות, עזרה, קהילה, תמיכה, מזון, מתנדבים"
      />
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