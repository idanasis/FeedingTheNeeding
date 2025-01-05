import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { slideUpVariants } from './animation';
import pic1 from '../images/pic1.jpg'
import pic2 from '../images/pic2.jpg'
import pic3 from '../images/pic3.jpg'
import pic4 from '../images/pic4.jpg'
import pic5 from '../images/pic5.jpg'
import pic6 from '../images/pic6.jpg'
import pic7 from '../images/pic7.jpg'
import pic8 from '../images/pic8.jpg'


const images = [
  pic1 , pic2, pic3, pic4, pic5, pic6, pic7, pic8
];

const DonateUs = () => {
  const [currentImage, setCurrentImage] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImage((prevImage) => (prevImage + 1) % images.length);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div id='לתרומות' className='w-full bg-gray-100'>
      <motion.div
        initial="hidden"
        whileInView="visible"
        variants={slideUpVariants}
        className='lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col lg:flex-row justify-between items-center gap-[20px]'
      >
        {/* Text and Button Section */}
        <motion.div
          className='w-full lg:w-2/3 flex flex-col items-center lg:items-start text-right gap-[20px]'
          variants={slideUpVariants}
        >
          <motion.h1
            variants={slideUpVariants}
            className='text-red-500 text-2xl font-bold'
          >
            תמכו במשימה שלנו
          </motion.h1>
          <motion.h1
            variants={slideUpVariants}
            className='text-black text-[40px] font-bold'
          >
            יחד, נשנה חיים
          </motion.h1>
          <motion.div
            className='w-[80px] h-[4px] bg-red-500'
            variants={slideUpVariants}
          ></motion.div>
          <motion.p
            variants={slideUpVariants}
            className='text-gray-700 text-lg mt-[20px] leading-relaxed'
          >
            עמותת "להשביע את הלב" מחויבת לעזור לאנשים ומשפחות שזקוקים לתמיכה.
            בזכות התרומה שלכם נוכל להעניק ארוחות מזינות, משאבים חיוניים, ותקווה
            לאלו שזקוקים לכך ביותר. הצטרפו אלינו והשפיעו כבר היום.
          </motion.p>
          <motion.button
            variants={slideUpVariants}
            className='mt-[30px] px-6 py-3 bg-red-500 text-white text-lg font-semibold rounded-lg hover:bg-red-600 transition duration-300'
            onClick={() => window.location.href = '/donation'}
          >
            לתרומה עכשיו
          </motion.button>
        </motion.div>

        {/* Image Carousel */}
        <motion.div
          className='w-full lg:w-1/3 h-[350px] relative rounded-lg'
          variants={slideUpVariants}
        >
          <div className="absolute inset-0 flex items-center justify-center overflow-hidden rounded-lg">
            <img
              src={images[currentImage]}
              alt='תמונה מתחלפת'
              className='w-full h-full object-contain p-4'
            />
          </div>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default DonateUs;