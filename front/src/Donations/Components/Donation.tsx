import React, { useState } from 'react';
import '../Styles/Donation.css';

// Import images
import QRCodeImg from '../Images/QRcode.png';
import CreditCardImg from '../Images/CreditCardPhoto.png';
import PayBoxImg from '../Images/payboxPhoto.png';
import BitImg from '../Images/bitPhoto.png';

const DonationPage: React.FC = () => {
    const [selectedOption, setSelectedOption] = useState<string | null>(null);
    const [isQrCodeEnlarged, setIsQrCodeEnlarged] = useState<boolean>(false);

    const handleOptionClick = (option: string) => {
        setSelectedOption(option);
        setIsQrCodeEnlarged(false);
    };

    const toggleQrCodeSize = () => {
        setIsQrCodeEnlarged((prev) => !prev);
    };

    return (
        <div className="donation-page">
            <div className="donation-container">
                <div className="donation-header">
                    <h1>תרום עכשיו</h1>
                    <p>בחר/י את הדרך הנוחה לך לתרום ולעזור לנו להביא שינוי אמיתי.</p>
                </div>

                <div className="donation-content">
                    {selectedOption === 'bit' && (
                        <div className="donation-method bit-method">
                            <h2>תרומה באמצעות ביט</h2>
                            <p className="bit-instructions">אנא סרקו את הקוד כדי לתרום</p>
                            <div
                                className={`qr-container ${isQrCodeEnlarged ? 'enlarged' : ''}`}
                                onClick={toggleQrCodeSize}
                            >
                                <img src={QRCodeImg} alt="Bit QR Code" className="qr-code" />
                            </div>
                        </div>
                    )}

                    {selectedOption === 'paybox' && (
                        <div className="donation-method paybox-method">
                            <h2>תרומה באמצעות PayBox</h2>
                            <a
                                href="https://payboxapp.page.link/BdKay5zV7s66nLmw5"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="paybox-link"
                            >
                                לחצו כאן לתרומה ב-PayBox
                            </a>
                        </div>
                    )}

                    {selectedOption === 'creditcard' && (
                        <div className="donation-method creditcard-method">
                            <h2>תרומה באמצעות כרטיס אשראי</h2>
                            <p>בקרוב תוכלו לתרום בצורה מאובטחת באמצעות כרטיס אשראי!</p>
                            <p>כדי לתרום כעת, לחצו על אחת מהאופציות האחרות למטה.</p>
                        </div>
                    )}

                    {!selectedOption && (
                        <p className="instructions">לחצו על אחת מהאפשרויות למטה לבחירת דרך התרומה.</p>
                    )}
                </div>

                <div className="donation-options">
                    <div
                        className={`donation-option ${selectedOption === 'bit' ? 'selected' : ''}`}
                        onClick={() => handleOptionClick('bit')}
                    >
                        <img src={BitImg} alt="Bit Logo" />
                    </div>

                    <div
                        className={`donation-option ${selectedOption === 'paybox' ? 'selected' : ''}`}
                        onClick={() => handleOptionClick('paybox')}
                    >
                        <img src={PayBoxImg} alt="PayBox Logo" />
                    </div>

                    <div
                        className={`donation-option ${selectedOption === 'creditcard' ? 'selected' : ''}`}
                        onClick={() => handleOptionClick('creditcard')}
                    >
                        <img src={CreditCardImg} alt="Credit Card Logo" />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DonationPage;