import { Swiper, SwiperSlide } from 'swiper/react';
import { bannerList } from '../../utils';
import { Pagination, EffectFade, Navigation, Autoplay } from 'swiper/modules';
import { Link } from 'react-router-dom';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/scrollbar';
import 'swiper/css/autoplay';
import 'swiper/css/effect-fade'


const HeroBanner = () => {
    const colors = ["bg-banner-color1", "bg-banner-color2" ,"bg-banner-color3"]
  return (
    <div className="py-2 rounded-md">
      <Swiper
        grabCursor={true}
        autoplay={{
          delay: 4000,
          disableOnInteraction: false,
        }}
        effect="fade"
        navigation
        pagination={{ clickable: true }}
        modules={[Pagination, EffectFade, Navigation, Autoplay]}
        slidesPerView={1}
      >
        {bannerList.map((item,i) => (
          <SwiperSlide key={item.id}>
            <div className={`carousel-item rounded-md sm:h-[500px] h-96 ${colors[i]}`}>
              <div className="flex items-center justify-center">
              <div className='hidden lg:flex justify-center w-1/2 p-8'> 
                <div className="text-center">
                  <h3 className="text-3xl text-white font-bold">{item.title}</h3>
                  <h1 className='text-5xl text-white font-bold mt-2'>
                    {item.subtitle}
                  </h1>
                  <p className='text-white font-bold'> 
                    {item.description}
                  </p>
                  <Link className='mt-6 inline-block bg-black text-white py-2 px-4 rounded hover:bg-gray-800'
                  to = "/products">
                    Shop
                  </Link>
                </div>
              </div>
            
            <div className='w-full flex justify-center lg:w-1/2 p-4'>
                <img src={item.image} alt="" />
            </div>
            </div>
            </div>
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default HeroBanner;
