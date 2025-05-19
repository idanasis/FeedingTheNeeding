// Landing.tsx
import React, { useState, useEffect } from 'react';
import {
    ChevronDown,
    Users,
    Shield, // Re-using existing icons, or you can add Heart, Gift, MessageSquare
    Cloud,
    Star,    // Will change how this is used or remove
    ArrowRight,
    Heart,   // Assuming Heart is available or added
    Gift,    // Assuming Gift is available or added
    MessageSquare // Assuming MessageSquare is available or added
} from 'lucide-react'; // Make sure these icons are available in your lucide-react version
import '../Styles/Landing.css';

interface Feature {
    icon: React.ReactNode;
    title: string;
    description: string;
}

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

    const features: Feature[] = [
        {
            icon: <Users className="feature-icon" />,
            title: "Community Support",
            description: "Access resources and connect with support networks in our community."
        },
        {
            icon: <Gift className="feature-icon" />, // Or another relevant icon like Users
            title: "Volunteer Opportunities",
            description: "Find meaningful ways to give back and help those in need."
        },
        {
            icon: <MessageSquare className="feature-icon" />, // Or another relevant icon like Shield
            title: "Direct Assistance Programs",
            description: "Get connected to programs offering help with essential needs and services."
        },
        {
            icon: <Heart className="feature-icon" />, // Or Cloud
            title: "Donate & Contribute",
            description: "Support our mission to nourish the heart of the community through your generosity."
        }
    ];

    return (
        <div className="landing-container">
            {/* Navigation */}
            <nav className={`navbar ${isScrolled ? 'navbar-scrolled' : 'navbar-transparent'}`}>
                <div className="navbar-content">
                    <div className="navbar-inner">
                        <div className="navbar-brand">
                            <span className="brand-text">Feeding The Needing</span>
                        </div>

                        <div className="navbar-menu">
                            <div className="navbar-links">
                                <a href="#home" className="nav-link">Home</a>
                                <a href="#about" className="nav-link">About</a>
                                <a href="#features" className="nav-link">Our Work</a>

                                <div className="dropdown">
                                    <button
                                        onClick={() => toggleDropdown('docs')}
                                        className="dropdown-button"
                                    >
                                        Resources
                                        <ChevronDown className="dropdown-icon" />
                                    </button>
                                    {activeDropdown === 'docs' && (
                                        <div className="dropdown-menu">
                                            <a href="#ard" className="dropdown-item">Impact Reports</a>
                                            <a href="#add" className="dropdown-item">Get Help Docs</a>
                                            <a href="#testing" className="dropdown-item">Volunteer Guides</a>
                                        </div>
                                    )}
                                </div>

                                <div className="dropdown">
                                    <button
                                        onClick={() => toggleDropdown('manuals')}
                                        className="dropdown-button"
                                    >
                                        Initiatives
                                        <ChevronDown className="dropdown-icon" />
                                    </button>
                                    {activeDropdown === 'manuals' && (
                                        <div className="dropdown-menu">
                                            <a href="#user-manual" className="dropdown-item">Food Programs</a>
                                            <a href="#maintenance-manual" className="dropdown-item">Shelter Assistance</a>
                                        </div>
                                    )}
                                </div>

                                <a
                                    href="https://github.com/idanasis/FeedingTheNeeding"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="nav-link"
                                >
                                    GitHub
                                </a>
                                <a href="#forum" className="nav-link">Community</a>
                                <a href="#contact" className="nav-link">Contact</a>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Hero Section */}
            <section id="home" className="hero-section">
                <div className="hero-background"></div>
                <div className="hero-content">
                    <div className="hero-text">
                        <h1 className="hero-title">
                            Feeding <span className="hero-title-accent">The Needing</span>
                        </h1>
                        <p className="hero-description">
                            A project by the "feed the heart" association, dedicated to connecting and supporting our community.
                        </p>
                    </div>

                    <div className="hero-buttons">
                        <a
                            href="https://singsync-447497864f8f.herokuapp.com" // Consider updating this URL
                            target="_blank"
                            rel="noopener noreferrer"
                            className="btn-primary"
                        >
                            <Heart className="btn-icon" /> {/* Changed Icon */}
                            Get Involved
                        </a>
                        <button className="btn-secondary">
                            Learn More
                            <ArrowRight className="btn-icon" />
                        </button>
                    </div>

                    <div className="hero-rating">
                        {/* Optional: Replace stars with something else or remove */}
                        {[1, 2, 3, 4, 5].map((star) => (
                            <Star key={star} className="star-icon" style={{color: "#2563eb"}} /> // Changed star color for a different feel
                        ))}
                        <span className="rating-text">Supported by our generous community</span>
                    </div>
                </div>

                {/* Floating elements */}
                <div className="floating-element floating-element-1"></div>
                <div className="floating-element floating-element-2"></div>
                <div className="floating-element floating-element-3"></div>
            </section>

            {/* Description Section */}
            <section className="description-section">
                <div className="section-content">
                    <h2 className="section-title">What We Do</h2>
                    <p className="description-text">
                        "Feeding The Needing" is a web platform by the "feed the heart" association.
                        Our mission is to provide resources, support, and a space for connection
                        to those in need within our community. Discover how we make a difference together.
                    </p>
                </div>
            </section>

            {/* Features Section */}
            <section id="features" className="features-section">
                <div className="features-container">
                    <div className="features-header">
                        <h2 className="section-title">Our Core Programs</h2>
                        <p className="section-subtitle">
                            Dedicated initiatives to support and uplift our community members.
                        </p>
                    </div>

                    <div className="features-grid">
                        {features.map((feature, index) => (
                            <div key={index} className="feature-card">
                                <div className="feature-icon-container">
                                    {feature.icon}
                                </div>
                                <h3 className="feature-title">{feature.title}</h3>
                                <p className="feature-description">{feature.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* About Section */}
            <section id="about" className="about-section">
                <div className="section-content">
                    <div className="about-content">
                        <h2 className="section-title">About "feed the heart"</h2>
                        <p className="about-text">
                            "Feeding The Needing" is a key initiative of the "feed the heart" association.
                            Leveraging technology (React, Spring Boot, and PostgreSQL), we aim to create a seamless platform
                            that supports and connects our community. Our mission is to make a tangible,
                            positive impact on people's lives by addressing essential needs and fostering connection.
                        </p>

                        <div className="stats-grid">
                            <div className="stat-item">
                                <div className="stat-number">1000+</div>
                                <div className="stat-label">Individuals Helped</div>
                            </div>
                            <div className="stat-item">
                                <div className="stat-number">500+</div>
                                <div className="stat-label">Volunteers Engaged</div>
                            </div>
                            <div className="stat-item">
                                <div className="stat-number">50+</div>
                                <div className="stat-label">Community Partners</div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="cta-section">
                <div className="section-content">
                    <h2 className="cta-title">Ready to Make a Difference?</h2>
                    <p className="cta-description">
                        Join "Feeding The Needing" and the "feed the heart" association to support our community.
                    </p>
                    <a
                        href="https://singsync-447497864f8f.herokuapp.com" // Consider updating this URL
                        target="_blank"
                        rel="noopener noreferrer"
                        className="btn-cta"
                    >
                        Join Our Mission
                        <ArrowRight className="btn-icon" />
                    </a>
                </div>
            </section>

            {/* Footer */}
            <footer className="footer">
                <div className="footer-content">
                    <div className="footer-grid">
                        <div className="footer-brand">
                            <div className="footer-logo">Feeding The Needing</div>
                            <p className="footer-description">
                                A project by the "feed the heart" association. Supporting our community, together.
                            </p>
                            <div className="social-links">
                                <a href="#" className="social-link">
                                    <span className="sr-only">Facebook</span>
                                    <div className="social-icon"></div> {/* Replace with actual icon if desired */}
                                </a>
                                <a href="#" className="social-link">
                                    <span className="sr-only">Twitter</span>
                                    <div className="social-icon"></div> {/* Replace with actual icon if desired */}
                                </a>
                                <a href="https://github.com/idanasis/FeedingTheNeeding" className="social-link" target="_blank" rel="noopener noreferrer">
                                    <span className="sr-only">GitHub</span>
                                    <div className="social-icon"></div> {/* Replace with actual icon if desired */}
                                </a>
                            </div>
                        </div>

                        <div className="footer-section">
                            <h3 className="footer-title">Resources</h3>
                            <ul className="footer-links">
                                <li><a href="#" className="footer-link">Impact Reports</a></li>
                                <li><a href="#" className="footer-link">Get Help</a></li>
                                <li><a href="#" className="footer-link">Volunteer</a></li>
                                <li><a href="#" className="footer-link">FAQs</a></li>
                            </ul>
                        </div>

                        <div className="footer-section">
                            <h3 className="footer-title">Association</h3>
                            <ul className="footer-links">
                                <li><a href="#about" className="footer-link">About "feed the heart"</a></li>
                                <li><a href="#" className="footer-link">Blog/News</a></li>
                                <li><a href="#" className="footer-link">Events</a></li>
                                <li><a href="#contact" className="footer-link">Contact Us</a></li>
                            </ul>
                        </div>
                    </div>

                    <div className="footer-bottom">
                        <p className="footer-copyright">
                            © 2025 Feeding The Needing by feed the heart. All rights reserved. Built with passion for our community.
                        </p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Landing;