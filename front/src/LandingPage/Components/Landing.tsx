import React, { useState, useEffect } from 'react';
// Using ChevronDown for dropdowns, and other icons as needed
import { Users, Heart, DollarSign, Info, ArrowRight, GitBranch, ChevronDown, Clock, CreditCard } from 'lucide-react';
import '../Styles/Landing.css'; // Assuming styles are already set up
import Logo from '../Images/logo.png';
import WebsiteMockupImage from '../Images/screen.png'; // IMPORT THE NEW IMAGE

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

                            <a href="#video" className="nav-link">Video</a>

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
                                        <a
                                            href="../../../public/Documents/UserManuel.pdf"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="dropdown-item"
                                        >
                                            User Manual
                                        </a>
                                        <a
                                            href="../../../public/Documents/MaintenanceManual.pdf"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="dropdown-item"
                                        >
                                            Maintenance Manual
                                        </a>
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
                                        <a
                                            href="../../../public/Documents/ARD.pdf"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="dropdown-item"
                                        >
                                            ARD
                                        </a>
                                        <a
                                            href="../../../public/Documents/ADD.pdf"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="dropdown-item"
                                        >
                                            ADD
                                        </a>
                                        <a
                                            href="../../../public/Documents/testing.pdf"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="dropdown-item"
                                        >
                                            Testing Document
                                        </a>
                                    </div>
                                )}
                            </div>

                            <a href="#involve" className="nav-link">Get Involved</a>
                            <a href="#impact" className="nav-link">Our Impact</a>
                            <a href="#mission" className="nav-link">About The Association</a>
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

            {/* Hero Section */}
            <section id="home" className="hero-section">
                <div className="hero-background"></div>
                <div className="hero-content">

                    {/* Right Column for the Website Mockup Image */}
                    <div className="hero-right-column">
                        <img
                            src={WebsiteMockupImage}
                            alt="Feeding the Needing Website on Desktop"
                            className="hero-website-mockup"
                        />
                    </div>

                    {/* Left Column for Text and Logo */}
                    <div className="hero-left-column">
                        <img src={Logo} alt="Feeding the Needing Logo" className="hero-main-logo"/>
                        <div className="hero-text">
                            <h1 className="hero-title">Feeding the Needing</h1>
                            {/*<p className="hero-description" style={{fontSize: '1.5rem', marginBottom: '1.5rem'}}>*/}
                            {/* Nourishing communities, one meal at a time.*/}
                            {/*</p>*/}
                            <p className="hero-description" style={{fontSize: '1.25rem', maxWidth: '40rem'}}>
                                We provide a comprehensive web-based system for volunteer management, task coordination,
                                and donations on behalf of the “Feed the Heart” association,
                                which delivers food and support to those in need in Be’er Sheva
                            </p>
                        </div>
                        <div className="hero-buttons">
                            <a href="#involve" className="btn-primary">
                                Get Involved <Users className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0.5rem' }}/>
                            </a>
                            <a href="#mission" className="btn-secondary">
                                Learn More <Info className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0.5rem' }}/>
                            </a>
                        </div>
                    </div>
                </div>
                <div className="floating-element floating-element-1"></div>
                <div className="floating-element floating-element-2"></div>
            </section>

            {/* About The Association Section */}
            <section id="mission" className="description-section">
                <div className="section-content">
                    <h2 className="section-title">About The Association</h2>
                    <p className="description-text" style={{ maxWidth: '50rem', lineHeight: '2' }}>
                        Since its founding in 2015, “Feed the Heart” has distributed hot meals and ready-to-eat food packages to families in need in Be’er Sheva.
                        These efforts were supported by collecting monetary donations and organizing manual deliveries.
                        Over the years, all processes were managed through manual record-keeping, telephone coordination, and delivery routes defined manually via WhatsApp by the coordination team and the association’s leadership
                    </p>
                </div>
            </section>

            {/* Our Impact Section */}
            <section id="impact" className="features-section" style={{paddingBottom: '3rem'}}>
                <div className="features-container">
                    <div className="features-header" style={{marginBottom: '3rem'}}>
                        <h2 className="section-title">Our Impact</h2>
                        <p className="section-subtitle">.Our web platform simplifies volunteer coordination and food delivery, making processes more efficient, transparent, and reliable</p>
                    </div>
                    <div className="features-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem' }}>
                        <div className="feature-card" style={{padding: '1.5rem'}}>
                            <div className="feature-icon-container" style={{marginBottom: '1rem'}}><Clock size={40}/></div>
                            <h3 className="feature-title" style={{fontSize: '1.2rem'}}>Faster Response</h3>
                            <p className="feature-description" style={{fontSize: '0.95rem'}}> By automating the documentation of every step and reducing response times, we improved
                                transparency, streamlined management, and saved dozens of hours of manual work each month</p>
                        </div>
                        <div className="feature-card" style={{padding: '1.5rem'}}>
                            <div className="feature-icon-container" style={{marginBottom: '1rem'}}><Users size={40}/></div>
                            <h3 className="feature-title" style={{fontSize: '1.2rem'}}>Centralized Platform</h3>
                            <p className="feature-description" style={{fontSize: '0.95rem'}}> Volunteers register,
                                report availability, and view tasks in real time<br/>
                                while those in need submit requests with just a few clicks</p>
                        </div>
                        <div className="feature-card" style={{padding: '1.5rem'}}>
                            <div className="feature-icon-container" style={{marginBottom: '1rem'}}><DollarSign size={40}/></div>
                            <h3 className="feature-title" style={{fontSize: '1.2rem'}}>Online Donations</h3>
                            <p className="feature-description" style={{fontSize: '0.95rem'}}>,QR codes and short URLs simplify giving <br/>
                                .making it fast and secure to support our cause</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Get Involved Section */}
            <section id="involve" className="about-section" style={{ backgroundColor: '#f9fafb', paddingTop: '4rem', paddingBottom: '4rem' }}>
                <div className="section-content">
                    <h2 className="section-title">Make a Difference</h2>
                    <p className="about-text" style={{maxWidth: '45rem', marginBottom: '1.5rem'}}>
                        .Your support is crucial to our mission<br/>
                        .Whether you can volunteer your time or simply help spread the word, every action counts
                    </p>
                    <div className="hero-down-buttons"> {/* Note: Re-using hero-buttons class here, might want a more generic name if styles differ */}
                        <a href="#volunteer-details" className="btn-primary">
                            To Our Website <ArrowRight className="btn-icon" style={{ marginLeft: '0.5rem', marginRight: '0.5rem' }}/>
                        </a>
                    </div>
                </div>
            </section>

            <footer id="footer-contact" className="footer">
                <div className="footer-content">
                    <div className="footer-grid" style={{textAlign: 'center'}}>
                        <div className="footer-brand" style={{gridColumn: '1 / -1', marginBottom: '1rem'}}>
                            <img src={Logo} alt="Feeding the Needing Logo" className="footer-logo-img" />
                        </div>
                        <div className="footer-section" style={{gridColumn: '1 / -1', textAlign: 'center', marginTop: '1rem'}}>
                            <ul className="footer-links" style={{flexDirection: 'row', justifyContent: 'center', gap: '1.5rem'}}>
                                <li><a href="#mission" className="footer-link">About The Association</a></li>
                                <li><a href="#impact" className="footer-link">Our Impact</a></li>
                                <li><a href="#involve" className="footer-link">Get Involved</a></li>
                                <li><a href="#documents-landing" className="footer-link">Documents</a></li>
                                <li><a href="#manuals-landing" className="footer-link">Manuals</a></li>
                            </ul>
                        </div>
                    </div>
                    <div className="footer-bottom">
                        <p className="footer-copyright">
                            © {new Date().getFullYear()} Eden & Idan & Daniel & Shavit. All Rights Reserved
                        </p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Landing;