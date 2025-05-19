import React, { useState, useEffect } from 'react';
// Using ChevronDown for dropdowns, and other icons as needed
import { Users, Heart, DollarSign, Info, ArrowRight, GitBranch, ChevronDown, FileText, BookOpen } from 'lucide-react';
import '../Styles/Landing.css'; // Assuming styles are already set up
import Logo from '../Images/logo.png';

const Landing: React.FC = () => {
    const [isScrolled, setIsScrolled] = useState<boolean>(false);
    const [activeDropdown, setActiveDropdown] = useState<string | null>(null);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 50);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const toggleDropdown = (dropdown: string) => {
        setActiveDropdown(activeDropdown === dropdown ? null : dropdown);
    };

    return (
        <div className="landing-container" dir="ltr">
            <nav className={`navbar ${isScrolled ? 'navbar-scrolled' : 'navbar-transparent'}`}>
                <div className="navbar-content">
                    <div className="navbar-inner">
                        {/* Spacer on the left to push content to the right */}
                        <div className="navbar-spacer-left"></div>

                        {/* Navigation links container */}
                        <div className="navbar-links-container">
                            <a
                                href="https://github.com/idanasis/FeedingTheNeeding"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="nav-link"
                            >
                                <GitBranch className="nav-icon-inline"/> GitHub
                            </a>

                            <div className="dropdown">
                                <button
                                    onClick={() => toggleDropdown('manuals')}
                                    className="dropdown-button"
                                >
                                    Manuals
                                    <ChevronDown className="dropdown-icon"/>
                                </button>
                                {activeDropdown === 'manuals' && (
                                    <div className="dropdown-menu">
                                        <a href="#user-manual" className="dropdown-item">User Manual</a>
                                        <a href="#maintenance-manual" className="dropdown-item">Maintenance Manual</a>
                                    </div>
                                )}
                            </div>

                            <div className="dropdown">
                                <button
                                    onClick={() => toggleDropdown('docs')}
                                    className="dropdown-button"
                                >
                                    Documents
                                    <ChevronDown className="dropdown-icon"/>
                                </button>
                                {activeDropdown === 'docs' && (
                                    <div className="dropdown-menu">
                                        <a href="#ard-document" className="dropdown-item">ARD</a>
                                        <a href="#add-document" className="dropdown-item">ADD</a>
                                        <a href="#testing-document" className="dropdown-item">Testing Document</a>
                                    </div>
                                )}
                            </div>

                            <a href="#involve" className="nav-link">Get Involved</a>
                            <a href="#impact" className="nav-link">Our Impact</a>
                            <a href="#mission" className="nav-link">Our Mission</a>
                            <a href="#home" className="nav-link">Home</a>

                        </div>

                        {/* Brand name now on the right */}
                        <div className="navbar-brand">
                            <a href="#home" className="brand-text-link">
                                <span className="brand-text">Feeding the Needing</span>
                            </a>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Hero Section - With prominent logo */}
            <section id="home" className="hero-section">
                <div className="hero-background"></div>
                <div className="hero-content">
                    <img src={Logo} alt="Feeding the Needing" className="hero-main-logo"/>
                    <div className="hero-text" style={{marginTop: '1rem'}}>
                        <h1 className="hero-title">Feeding the Needing</h1>
                        <p className="hero-description" style={{fontSize: '1.5rem', marginBottom: '2rem'}}>
                        Nourishing communities, one meal at a time.
                        </p>
                        <p className="hero-description" style={{fontSize: '1.15rem', maxWidth: '45rem'}}>
                            We provide essential food support and compassionate care to individuals and families facing
                            hardship. Join us in making a difference.
                        </p>
                    </div>
                    <div className="hero-buttons">
                        <a href="#involve" className="btn-primary">
                            Get Involved <Users className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0' }}/>
                        </a>
                        <a href="#mission" className="btn-secondary">
                            Learn More <Info className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0' }}/>
                        </a>
                    </div>
                </div>
                <div className="floating-element floating-element-1"></div>
                <div className="floating-element floating-element-2"></div>
            </section>

            {/* Our Mission Section */}
            <section id="mission" className="description-section">
                <div className="section-content">
                    <h2 className="section-title">Our Mission</h2>
                    <p className="description-text" style={{ maxWidth: '50rem', lineHeight: '1.7' }}>
                        Feeding the Needing is a non-profit organization committed to alleviating hunger and providing vital support to vulnerable members of our community. Since 2015, we've been delivering hot meals, food packages, and a helping hand to families, the elderly, and those in crisis. We believe in the power of community and compassion to create positive change.
                    </p>
                </div>
            </section>

            {/* Our Impact Section */}
            <section id="impact" className="features-section" style={{paddingBottom: '3rem'}}>
                <div className="features-container">
                    <div className="features-header" style={{marginBottom: '3rem'}}>
                        <h2 className="section-title">Our Impact</h2>
                        <p className="section-subtitle">Dedicated to making a tangible difference every day.</p>
                    </div>
                    <div className="features-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem' }}>
                        <div className="feature-card" style={{padding: '1.5rem'}}>
                            <div className="feature-icon-container" style={{marginBottom: '1rem'}}><Heart size={40}/></div>
                            <h3 className="feature-title" style={{fontSize: '1.2rem'}}>Nutritious Meals</h3>
                            <p className="feature-description" style={{fontSize: '0.95rem'}}>Delivering hot food and essential grocery packages.</p>
                        </div>
                        <div className="feature-card" style={{padding: '1.5rem'}}>
                            <div className="feature-icon-container" style={{marginBottom: '1rem'}}><Users size={40}/></div>
                            <h3 className="feature-title" style={{fontSize: '1.2rem'}}>Community Support</h3>
                            <p className="feature-description" style={{fontSize: '0.95rem'}}>Offering a listening ear and guidance to those in need.</p>
                        </div>
                        <div className="feature-card" style={{padding: '1.5rem'}}>
                            <div className="feature-icon-container" style={{marginBottom: '1rem'}}><DollarSign size={40}/></div>
                            <h3 className="feature-title" style={{fontSize: '1.2rem'}}>Volunteer Driven</h3>
                            <p className="feature-description" style={{fontSize: '0.95rem'}}>Powered by the generosity and dedication of our volunteers.</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Get Involved Section */}
            <section id="involve" className="about-section" style={{ backgroundColor: '#f9fafb', paddingTop: '4rem', paddingBottom: '4rem' }}>
                <div className="section-content">
                    <h2 className="section-title">Make a Difference</h2>
                    <p className="about-text" style={{ maxWidth: '45rem', marginBottom: '2.5rem' }}>
                        Your support is crucial to our mission. Whether you can volunteer your time or simply help spread the word, every action counts.
                    </p>
                    <div className="hero-buttons">
                        <a href="#volunteer-details" className="btn-primary">
                            Volunteer Opportunities <ArrowRight className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0' }}/>
                        </a>
                        <a href="#request-help-form" className="btn-secondary">
                            Request Assistance <Info className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0' }} />
                        </a>
                    </div>
                </div>
            </section>

            <footer id="footer-contact" className="footer">
                <div className="footer-content">
                    <div className="footer-grid" style={{textAlign: 'center'}}>
                        <div className="footer-brand" style={{gridColumn: '1 / -1', marginBottom: '1rem'}}>
                            <img src={Logo} alt="Feeding the Needing Logo" className="footer-logo-img" />
                            <p className="footer-description" style={{fontSize: '1rem', marginTop: '0.5rem'}}>
                                For inquiries, please email: <a href="mailto:info@feedingtheneeding.org" className="footer-link">info@feedingtheneeding.org</a>
                            </p>
                        </div>
                        <div className="footer-section" style={{gridColumn: '1 / -1', textAlign: 'center', marginTop: '1rem'}}>
                            <ul className="footer-links" style={{flexDirection: 'row', justifyContent: 'center', gap: '1.5rem'}}>
                                <li><a href="#mission" className="footer-link">Our Mission</a></li>
                                <li><a href="#impact" className="footer-link">Our Impact</a></li>
                                <li><a href="#involve" className="footer-link">Get Involved</a></li>
                                <li><a href="#documents-landing" className="footer-link">Documents</a></li>
                                <li><a href="#manuals-landing" className="footer-link">Manuals</a></li>
                            </ul>
                        </div>
                    </div>
                    <div className="footer-bottom">
                        <p className="footer-copyright">
                            © {new Date().getFullYear()} Shavit & Idan & Daniel & Eden. All Rights Reserved.
                        </p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Landing;