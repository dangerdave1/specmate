	import './support/gentypes';
	import { Proxy } from './support/proxy';


	export class BDDConnection  {

		___nsuri: string = "http://specmate.com/20180925/model/bdd";
		public url: string;
		public className: string = "BDDConnection";
		public static className: string = "BDDConnection";

		// Attributes
		public id: EString;
		public name: EString;
		public description: EString;
		public negate: EBoolean;

		// References
		
		public tracesTo: Proxy[];
		public tracesFrom: Proxy[];
		public source: Proxy;
		public target: Proxy;

		// Containment


	}

