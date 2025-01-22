import React from 'react'
import {motion} from 'framer-motion';
import { slideUpVariants ,zoomInVariants} from './animation';
import {oneMan} from './export';

const About = () => {
  return (
    <div id='אודות'className='lg:w-[80%] w-[90%] m-auto py-[60px] flex lg:flex-row flex-col items-start gap-[50px] justify-between'>
      <motion.div
       initial="hidden" whileInView="visible" variants={slideUpVariants} className=' lg:w-[60%] w-full flex flex-col justify-center items-start gap-1'>
        <motion.h1 variants={slideUpVariants} className='text-red-500 text-2xl' >
          חזון
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-white uppercase text-[40px] font-bold' >
          "תן לו משלו, שאתה ושלך-שלו" (פרקי אבות, פרק ג')
        </motion.h1>
        <div className='w-[120px] h-[6px] bg-red-500'></div>
          {oneMan.map((item, index) => (
                  <div
                    key={index}
                    className="flex flex-col justify-center items-center"
                  >
                    <div className="  p-3 pb-[70px] pt-[30px]">
                      <p className="text-lg text-[22px] text-center text-white">{item.about}</p>
                    </div>
                    <div className="flex flex-col justify-center items-center gap-[5px]">
                    <img
                      src={item.image}
                      alt="icon"
                      className="mt-[-50px] rounded-full"
                      width="100"
                      height="100"
                    />
                     <h1 className="text-white text-27px font-semibold uppercase">{item.name}</h1>
                     <h1 className="text-red-500 text-[22px]"> {item.post}</h1>
                      </div> 
                    </div>
                ))}
      </motion.div>
      <motion.div
       variants={slideUpVariants} className='lg:w-[40%] w-full flex flex-col justify-center items-start gap-6'
      >
        <p className='text-white text-lg text-justify'>
          <span className='text-red-500'>"להשביע את הלב"</span> הוקם בשנת 2015 ופועל בכל ימות השנה בכלל ובחגים וסופי
שבוע בפרט ע"י גיוס תרומות.

"להשביע את הלב" פועלים למען משפחות, קשישים, פגועי נפש, אמהות חד הוריות, בעלי נכות נפשית ופיזית כאחד.

המיזם מלווה משפחות ועוזר לאלה שנקלעו למשברים ורוצים להשתקם ולהתקדם בחייהם.
המיזם דואג שקשישים רבים יעבירו את השבת והחגים עם אוכל חם וטרי,
ואף דואג למצות איתם את הזכויות המגיעות להם.

כיום גודל מערך ההתנדבות שלנו מתפרס לכ200 בתי אב בניהם קשישים ומשפחות קשיי יכולת. 
אנחנו פונים לאוכלוסיות במצוקה ומנסים לעבור עימם תהליך שיקום הכולל הספקת ארוחה חמה, אוזן קשבת והכוונה בערוצים הנכונים. 
יחד עם זאת אחת לחודש נאספים סלי מזון עשירים שמסופקים למשפחות (חלבי, בשרי, יבשים, ירקות ופירות).
        </p>
    
        </motion.div>
    </div>
  )
}

export default About;