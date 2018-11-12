	import './support/gentypes';
	import { Proxy } from './support/proxy';


	export class BDDNode  {

		___nsuri: string = "http://specmate.com/20180925/model/bdd";
		public url: string;
		public className: string = "BDDNode";
		public static className: string = "BDDNode";

		// Attributes
		public id: EString;
		public name: EString;
		public description: EString;
		public x: EDouble;
		public y: EDouble;

		// References
		
		public tracesTo: Proxy[];
		public tracesFrom: Proxy[];
		public outgoingConnections: Proxy[];
		public incomingConnections: Proxy[];

		// Containment


	}

