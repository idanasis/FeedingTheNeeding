import React from 'react'
import {motion} from 'framer-motion';
import { slideUpVariants ,zoomInVariants} from './animation';
const About = () => {
  return (
    <div id='about'className='lg:w-[80%] w-[90%] m-auto py-[60px] flex lg:flex-row flex-col items-start gap-[50px] justify-between'>
      <motion.div
       initial="hidden" whileInView="visible" variants={slideUpVariants} className=' lg:w-[60%] w-full flex flex-col justify-center items-start gap-6'>
        <motion.h1 variants={slideUpVariants} className='text-red-500 text-2xl' >
          מי אנחנו?
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-white uppercase text-[40px] font-bold' >
עמותת להשביע את הלב        </motion.h1>
        <div  className='w-[120px] h-[6px] bg-red-500'></div>
        <p className='text-3xl italic text-gray-50 mt-[60px]'>אוכל חם לנזקקים אחלה של דבר באמת כדאי חבל לפספס זה טוב מאוד</p>
      </motion.div>
      <motion.div
       variants={slideUpVariants} className='lg:w-[40%] w-full flex flex-col justify-center items-start gap-6'
      >
        <p className='text-white text-lg text-justify'>בבאר שבע, עיר שוקקת חיים, גילינו מציאות קשה: משפחות רבות נאבקות מדי יום כדי להעמיד על שולחן ארוחה מזינה. מתוך הכרה בצורך הבסיסי הזה, ובחזון של עיר שבה כל אחד יכול לחוות ביטחון תזונתי, הוקמה עמותת 'להשביע את הלב'.

הסיפור שלנו החל מיוזמה קטנה של מתנדבים שזיהו את המצוקה הגוברת בקהילה. עם הזמן, החזון התרחב והיום 'להשביע את הלב' מספקת אלפי ארוחות חמות וסלי מזון למשפחות נזקקות מדי חודש. הסיפורים של האנשים שאנו עוזרים להם ממלאים אותנו בתקווה ובכוח להמשיך ולפעול. כל ארוחה שאנו מחלקים היא לא רק מזון, אלא גם סימן של חמלה ואכפתיות</p>
        <motion.button
  variants={zoomInVariants}
  className='bg-red-500 hover:bg-white hover:text-black px-10 py-3 rounded-lg text-black font-bold'
  >
קרא עוד
  </motion.button>
        </motion.div>
    </div>
  )
}

export default About;