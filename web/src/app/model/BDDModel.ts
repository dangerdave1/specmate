	import './support/gentypes';
	import { Proxy } from './support/proxy';


	export class BDDModel  {

		___nsuri: string = "http://specmate.com/20180925/model/bdd";
		public url: string;
		public className: string = "BDDModel";
		public static className: string = "BDDModel";

		// Attributes
		public id: EString;
		public name: EString;
		public description: EString;

		// References
		
		public tracesTo: Proxy[];
		public tracesFrom: Proxy[];

		// Containment


	}

