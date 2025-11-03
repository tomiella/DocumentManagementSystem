// placement: A - Logo

// import logo from '../assets/logo.png';
import logo from '../assets/logo2.png';
// import logo from '../assets/logoPaperless.png'


export default function Logo() {
    return(
        <div className="flex items-center gap-2">
            <img src={logo} alt="PaperLess TM" className="h-8 w-auto"/>
            <h1 className="text-xl font-bold mb-0">Paperless ™ </h1>

        </div>
        )
}